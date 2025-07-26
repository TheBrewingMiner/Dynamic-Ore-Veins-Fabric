package net.thebrewingminer.dynamicoreveins.helper;

public record HeightRangeWrapper(int min_y, int max_y){
    @Override
    public String toString(){
        return ("Min_Y: " + this.min_y + ", Max_Y: " + this.max_y);
    }
}