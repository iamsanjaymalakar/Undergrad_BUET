public class Crew {

    private Meal meal = new Meal();

    public void addItem(Item item)
    {
        meal.addItem(item);
    }

    public Meal getMeal() {
        return meal;
    }
}
