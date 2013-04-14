import static java.lang.Math.exp;
import static java.lang.Math.log;
import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.math3.special.Gamma.logGamma;

public class Percentiles {
	// Returns the index interval for the 95% confidence interval of a
	// percentile
	public static double[] confidenceInterval(final double p, final int n) {
		final double target = (1.0 - 0.95) / 2.0;

		return new double[] { //
		accumulateToTarget(n, p, target), //
				accumulateToTarget(n, p, 1.0 - target) };
	}

	private static double accumulateToTarget(final int n, final double p,
			final double target) {
		isTrue(0.0 <= target && target <= 1.0);
		isTrue(0.0 <= p && p <= 1.0);

		if (target > 0.5) {
			return (n - 1) - accumulateToTarget(n, 1.0 - p, 1.0 - target);
		}

		double total = 0.0;
		for (int k = 0; k <= n; k++) {
			final double mass = binomialMass(n, k, p);
			final double w = (target - total) / mass;
			if (w <= 1.0) {
				final double result = k - 1 + w;
				isTrue(-1 <= result && result <= n);
				return result;
			}

			total += mass;
		}
		throw new RuntimeException("Bug bug!");
	}

	public static double binomialMass(final int n, final int k, final double p) {
		isTrue(0 <= k && k <= n);
		isTrue(0.0 <= p && p <= 1.0);
		final double logResult = logChoose(n, k) + //
				k * log(p) + //
				(n - k) * log(1 - p);

		final double result = exp(logResult);
		isTrue(0.0 <= result && result <= 1.0);
		return result;
	}

	private static double logChoose(final int n, final int k) {
		isTrue(0 <= k && k <= n);
		return logFactorial(n) - logFactorial(k) - logFactorial(n - k);
	}

	private static double logFactorial(int n) {
		return logGamma(n + 1);
	}
}
