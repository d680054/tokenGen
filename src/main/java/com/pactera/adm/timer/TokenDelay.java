package com.pactera.adm.timer;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Created by David.Zheng on 24/11/2016.
 */
public class TokenDelay implements Delayed
{
	private String key;

	private String token;

	private long expires;

	private long endTime;

	public TokenDelay(String key)
	{
		this.key = key;
	}

	public TokenDelay(String key, String token, long timeToLive)
	{
		this.key = key;
		this.token = token;
		this.expires = timeToLive;
		this.endTime = System.currentTimeMillis() + timeToLive * 1000 - 300 * 1000;
	}

	public String getKey()
	{
		return key;
	}

	public String getToken()
	{
		return token;
	}

	@Override public boolean equals(Object o)
	{
		if (this == o)
			return true;

		if (o == null || getClass() != o.getClass())
			return false;

		TokenDelay that = (TokenDelay) o;

		return new org.apache.commons.lang3.builder.EqualsBuilder()
				.append(key, that.key)
				.isEquals();
	}

	@Override public int hashCode()
	{
		return new HashCodeBuilder(17, 37)
				.append(key)
				.toHashCode();
	}

	/**
	 * Returns the remaining delay associated with this object, in the
	 * given time unit.
	 *
	 * @param unit the time unit
	 * @return the remaining delay; zero or negative values indicate
	 * that the delay has already elapsed
	 */
	@Override public long getDelay(TimeUnit unit)
	{
		return endTime - System.currentTimeMillis();
	}

	/**
	 * Compares this object with the specified object for order.  Returns a
	 * negative integer, zero, or a positive integer as this object is less
	 * than, equal to, or greater than the specified object.
	 * <p/>
	 * <p>The implementor must ensure <tt>sgn(x.compareTo(y)) ==
	 * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
	 * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
	 * <tt>y.compareTo(x)</tt> throws an exception.)
	 * <p/>
	 * <p>The implementor must also ensure that the relation is transitive:
	 * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
	 * <tt>x.compareTo(z)&gt;0</tt>.
	 * <p/>
	 * <p>Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
	 * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
	 * all <tt>z</tt>.
	 * <p/>
	 * <p>It is strongly recommended, but <i>not</i> strictly required that
	 * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
	 * class that implements the <tt>Comparable</tt> interface and violates
	 * this condition should clearly indicate this fact.  The recommended
	 * language is "Note: this class has a natural ordering that is
	 * inconsistent with equals."
	 * <p/>
	 * <p>In the foregoing description, the notation
	 * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
	 * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
	 * <tt>0</tt>, or <tt>1</tt> according to whether the value of
	 * <i>expression</i> is negative, zero or positive.
	 *
	 * @param o the object to be compared.
	 * @return a negative integer, zero, or a positive integer as this object
	 * is less than, equal to, or greater than the specified object.
	 * @throws NullPointerException if the specified object is null
	 * @throws ClassCastException   if the specified object's type prevents it
	 *                              from being compared to this object.
	 */
	@Override public int compareTo(Delayed o)
	{
		TokenDelay md5Message = (TokenDelay) o;
		return endTime - md5Message.endTime > 0 ? 1 : 0;
	}
}
