package map;

import java.util.HashMap;

public class Point {

    private HashMap<IngredientType, Ingredient> ingredients;

    public HashMap<IngredientType, Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(HashMap<IngredientType, Ingredient> ingredients) {
        this.ingredients = ingredients;
    }
}
