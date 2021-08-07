public class Customer {

    public static void main(String args[])
    {
        Cashier cashier = new Cashier();  // created Cashier

        Crew crew1= new Crew();  // Created to crews
        Crew crew2= new Crew();

        cashier.ComboOne(crew1); // Cashier tells crew1 to prepate ComboOne
        Meal comboOneMeal = crew1.getMeal(); // crew1 returns the meal
        comboOneMeal.showItems(); // printng the items of meal

        cashier.ComboTwo(crew2); // Cashier tells crew1 to prepate ComboOne
        Meal comboTwoMeal = crew2.getMeal(); // crew1 returns the meal
        comboTwoMeal.showItems(); // printng the items of meal
    }
}
