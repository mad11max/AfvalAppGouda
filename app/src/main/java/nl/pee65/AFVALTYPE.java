
package nl.pee65;


import nl.pee65.R;

/**
 * The Enum AFVALTYPE.
 */
public enum AFVALTYPE {
    // icon?

    /** The GRIJS. */
    GRIJS(R.drawable.grijs),
    /** The BLAUW. */
    BLAUW(R.drawable.blauw),

    /** The GROEN. */
    GROEN(R.drawable.groen),
    /** The ORANJE. */
    ORANJE(R.drawable.oranje),
    ZAK(R.drawable.zak);

    /** The resource icon. */
    private int resourceIcon;

    /**
     * Instantiates a new aFVALTYPE.
     * 
     * @param resourceIcon
     *            the resource icon
     */
    private AFVALTYPE(int resourceIcon) {
        this.resourceIcon = resourceIcon;
    }

    /**
     * Gets the resource icon.
     * 
     * @return the resource icon
     */
    public int getResourceIcon() {
        return resourceIcon;
    }
}
