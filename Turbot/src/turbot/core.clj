; :mode=clojure:

;; ## IRC bot showing recent chat history over http ##

(ns turbot.core
    (:gen-class)
    (:use
        [clojure.contrib.command-line]
        [irclj.core]
        [ring.adapter.jetty :only [run-jetty]]
        [ring.util.response :only [response redirect]]
        [ring.util.json-response :only [json-response]]
        [ring.middleware.resource :only [wrap-resource]]
    )
)

(def empty-state {})

(defn on-any [allow-privmsg {:keys [
    channel
    irc
    nick
    new-nick
    doing
    message
    target
    reason
    action?
    topic]} state-ref]
    (if (= (= doing "PRIVMSG") allow-privmsg)
        (dosync
            (if-let [channel-obj ((@irc :channels) channel)] (ref-set state-ref
                (assoc @state-ref channel
                    (assoc (@state-ref channel)
                        :users (sort (map first (channel-obj :users)))
                        :topic (channel-obj :topic)))))
            (if channel
                (ref-set state-ref (assoc-in @state-ref [channel :recentlines]
                    (take 1000
                        (concat (:recentlines (@state-ref channel))
                            [(into {} (filter val {
                                :nick nick
                                :newnick new-nick
                                :doing doing
                                :message message
                                :target target
                                :reason reason
                                :isaction action?
                                :topic topic
                            }))]))))))))

(defn web-handler [{uri :uri} state-ref]
    (condp = uri
        "/chat.json" (assoc-in (json-response @state-ref)
            [:headers "Cache-Control"] "max-age=2")
        "/" (redirect "/chat.html")))

(defn -main
    [& args]
    (with-command-line
        args
        "Arguments: -channel #mychannel -nick botnick -server irc.server.com"
        [
            [channel c "IRC Channel to join." "#sprafftest"]
            [nick n "Bot's IRC nick." "turbot"]
            [server s "IRC server address." "localhost"]
        ]
        (let [state-ref (ref empty-state)] (do
            (future (run-jetty (wrap-resource
                #(web-handler % state-ref) "public") {:port 3000}))
            (connect
                (create-irc {
                    :name nick
                    :server server
                    :fnmap {
                        :on-message (fn [e]
                            (on-any true e state-ref))
                        :on-any (fn [e]
                            (on-any false e state-ref))
                    }
                })
                :channels [channel])
))))


