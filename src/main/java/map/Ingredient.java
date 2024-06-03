package map;

public class Ingredient {

private final IngredientType ingredientType;

private int quantity = 0;

    public Ingredient(IngredientType ingredientType, int quantity) {
        this.ingredientType = ingredientType;
        this.quantity = quantity;
    }

    public IngredientType getIngredientType() {
        return ingredientType;
    }
    public int getQuantity() {
        return quantity;
    }
}