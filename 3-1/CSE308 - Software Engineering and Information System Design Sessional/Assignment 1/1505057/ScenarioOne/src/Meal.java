import java.util.ArrayList;
import java.util.List;

public class Meal {

    private List<Item> items = new ArrayList<Item>();

    public void addItem(Item item)
    {
        items.add(item);
    }

    public float getCost()
    {
        float sum=0;
        for(Item it : items)
        {
            sum+=it.price();
        }
        return sum;
    }

    public void showItems()
    {
        System.out.println(" "+"Name"+" - "+"Packing"+" - "+"Price");
        for(Item it : items)
        {
            System.out.println(" "+it.name()+" - "+it.packing().pack()+" - "+it.price());
        }
        System.out.println("\n"+" Total cost : "+getCost());
        System.out.println("\n\n");
    }


}
