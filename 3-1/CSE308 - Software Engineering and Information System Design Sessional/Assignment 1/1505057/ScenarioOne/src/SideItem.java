public abstract class SideItem implements Item{
    @Override
    public Packing packing()
    {
        return new Bag();
    }
}
