public abstract class MainItem implements Item{
    @Override
    public Packing packing()
    {
        return new Bag();
    }
}
