/*
    Copyright (c) 2005-2007, Paul Richards
    All rights reserved.

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions are met:

        * Redistributions of source code must retain the above copyright notice,
        this list of conditions and the following disclaimer.

        * Redistributions in binary form must reproduce the above copyright
        notice, this list of conditions and the following disclaimer in the
        documentation and/or other materials provided with the distribution.

        * Neither the name of Paul Richards nor the names of contributors may be
        used to endorse or promote products derived from this software without
        specific prior written permission.

    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
    AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
    IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
    ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
    LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
    CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
    SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
    INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
    CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
    ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
    POSSIBILITY OF SUCH DAMAGE.
*/

package pigeon.competitions;

public abstract class Competition
{
    /**
        The textual name of the competition as it
        appears on reports etc.
    */
    protected final String name;
    
    /**
        The cost per bird to enter.
    */
    protected final double entryCost;
    
    /**
        The fraction of the total prize fund that is kept by the
        club before prizes are calculated.
    */
    protected final double clubTake;
    
    public Competition(String name, double entryCost, double clubTake)
    {
        this.name = name;
        this.entryCost = entryCost;
        this.clubTake = clubTake;
    }
    
    public String getName()
    {
        return name;
    }
    
    protected void checkPlaceIsInRange(int place, int entrants) throws IllegalArgumentException
    {
        if (place < 1 || place > maximumNumberOfWinners(entrants)) {
            throw new IllegalArgumentException("Place expected to be within the range [1, maximumNumberOfWinners(entrants)].");
        }
    }
    
    /**
        Given the number of entrants for this competition,
        calculate the maximum number of winners there could
        be.  The actual number of winners may be smaller if
        not enough birds complete the race.
    */
    public abstract int maximumNumberOfWinners(int entrants);
    
    /**
        Given the position that a bird has come in the competition
        and the total number of entrants, calculate the prize.
     
        @param place Must be in the range [1, maximumNumberOfWinners(entrants)].
    */
    public abstract double prize(int place, int entrants);
    
    public double totalPoolMoney(int entrants)
    {
        return entrants * entryCost;
    }
    
    public double totalClubTake(int entrants)
    {
        return totalPoolMoney(entrants) * clubTake;
    }
}