public abstract class Toy implements Item{
    @Override
    public Packing packing()
    {
        return new Bag();
    }
}
