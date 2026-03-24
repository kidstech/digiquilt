package umm.digiquilt.model;

/**
 * Enum for the shapes that are offered in the Shape Selector. 
 *
 */
public enum Shape {

    /*
     * Tiles are arranged like this:
     *   0    4
     *  1 2  5  6
     *   3    7
     *   8    12
     *  9 10 13 14
     *   11   15
     * 
     */
    
    /**
     * Full sized square, entire Patch is covered in one color.
     */
    FULLSQUARE (new String[] {
            "Fabric", "Fabric", "Fabric", "Fabric",
            "Fabric", "Fabric", "Fabric", "Fabric",
            "Fabric", "Fabric", "Fabric", "Fabric",
            "Fabric", "Fabric", "Fabric", "Fabric",
    }), 

    /**
     * Isosceles right triangle. Half the area of the full Patch is covered.
     */
    HALFTRIANGLE(new String[] {
            "TRANSPARENT", "TRANSPARENT", "TRANSPARENT", "TRANSPARENT",
            "TRANSPARENT", "TRANSPARENT", "Fabric", "Fabric",
            "TRANSPARENT", "TRANSPARENT", "Fabric", "Fabric",
            "Fabric", "Fabric", "Fabric", "Fabric",
    }), 
    /**
     * Rectangle half the size of the full square.
     */
    HALFRECTANGLE(new String[] {
            "Fabric", "Fabric", "Fabric", "Fabric",
            "Fabric", "Fabric", "Fabric", "Fabric",
            "TRANSPARENT", "TRANSPARENT", "TRANSPARENT", "TRANSPARENT",
            "TRANSPARENT", "TRANSPARENT", "TRANSPARENT", "TRANSPARENT",
    }), 
    /**
     * Isosceles right triangle, one quarter of the full Patch size.
     */
    QUARTERTRIANGLE(new String[] {
            "TRANSPARENT", "TRANSPARENT", "Fabric", "Fabric",
            "TRANSPARENT", "Fabric", "TRANSPARENT", "Fabric",
            "TRANSPARENT", "TRANSPARENT", "TRANSPARENT", "TRANSPARENT",
            "TRANSPARENT", "TRANSPARENT", "TRANSPARENT", "TRANSPARENT",
    }), 
    /**
     * Square that is one quarter size of the full square.
     */
    QUARTERSQUARE(new String[] {
            "Fabric", "Fabric", "Fabric", "Fabric",
            "TRANSPARENT", "TRANSPARENT", "TRANSPARENT", "TRANSPARENT",
            "TRANSPARENT", "TRANSPARENT", "TRANSPARENT", "TRANSPARENT",
            "TRANSPARENT", "TRANSPARENT", "TRANSPARENT", "TRANSPARENT",
    });
    
    /**
     * The String array that the patch will be created from. Any string that
     * isn't equal to "TRANSPARENT" will be replaced by a color
     */
    private String[] template;
    
    /**Create a new Shape. The Patch returned will be created based on
     * the template given -- any string that isn't "TRANSPARENT" will be
     * replaced by a fabric color.
     * @param template
     */
    Shape(String[] template){
        this.template = template;
    }
    
    /**
     * Get this shape in the given color as a Patch.
     * @param aFabric The desired color
     * @return a Patch with this shape in that color
     * 
     */
    public Patch getPatch(Fabric aFabric) {
        Fabric[] shape = new Fabric[Patch.MAXTILES];
        for (int i = 0; i<Patch.MAXTILES; i++){
            if (!template[i].equals("TRANSPARENT")){
                shape[i] = aFabric;
            } else {
                shape[i] = Fabric.TRANSPARENT;
            }
        }
        return new Patch(shape);
    }
    
}
