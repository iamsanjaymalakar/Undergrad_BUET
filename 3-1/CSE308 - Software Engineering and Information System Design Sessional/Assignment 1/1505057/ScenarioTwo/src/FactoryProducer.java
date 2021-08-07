public class FactoryProducer {
    public static AbstractFactory getFactory(String factory)
    {
        if(factory.equalsIgnoreCase("display"))
            return new DisplayDriverFactory();
        else if(factory.equalsIgnoreCase("print"))
            return new PrintDriverFactory();
        return null;
    }
}
