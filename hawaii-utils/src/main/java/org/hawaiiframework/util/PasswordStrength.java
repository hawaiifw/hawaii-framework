package org.hawaiiframework.util;

/**
 * Object to indicate the strength of a password.
 */
public class PasswordStrength {

    /**
     * The strength.
     */
    private int strength;

    /**
     * The password is too short.
     */
    private boolean tooShort;

    /**
     * The password is weak.
     */
    private boolean weak;


    /**
     * Get the strength.
     *
     * @return The strength.
     **/
    public int getStrength() {
        return strength;
    }

    /**
     * Set the strength property.
     *
     * @param strength The strength property.
     **/
    public void setStrength(final int strength) {
        this.strength = strength;
    }

    /**
     * Returns whether tooShort is true.
     *
     * @return True when tooShort is true. False otherwise.
     **/
    public boolean isTooShort() {
        return tooShort;
    }

    /**
     * Set the tooShort property.
     *
     * @param tooShort The tooShort property.
     **/
    public void setTooShort(final boolean tooShort) {
        this.tooShort = tooShort;
    }

    /**
     * Returns whether weak is true.
     *
     * @return True when weak is true. False otherwise.
     **/
    public boolean isWeak() {
        return weak;
    }

    /**
     * Set the weak property.
     *
     * @param weak The weak property.
     **/
    public void setWeak(final boolean weak) {
        this.weak = weak;
    }
}
