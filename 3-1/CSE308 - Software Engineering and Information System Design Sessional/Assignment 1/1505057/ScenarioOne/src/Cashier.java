public class Cashier {
    public void ComboOne(Crew crew)
    {
        crew.addItem(new Hamburger());  // MainItem
        crew.addItem(new Fries());  // SideIetm
        crew.addItem(new Dinosaur()); // Toy
        crew.addItem(new Coke()); // Drink
    }

    public void ComboTwo(Crew crew)
    {
        crew.addItem(new Cheeseburger());  // MainItem
        crew.addItem(new Fries());  // SideIetm
        crew.addItem(new Dinosaur()); // Toy
        crew.addItem(new Coke()); // Drink
    }

    public void ComboThree(Crew crew)
    {
        crew.addItem(new Chicken());  // MainItem
        crew.addItem(new Fries());  // SideIetm
        crew.addItem(new Dinosaur()); // Toy
        crew.addItem(new Coke()); // Drink
    }

}
