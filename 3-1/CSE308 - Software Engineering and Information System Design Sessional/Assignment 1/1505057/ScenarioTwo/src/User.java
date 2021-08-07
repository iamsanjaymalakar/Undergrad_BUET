public class User {
    public static void main(String args[])
    {
        // declaring cpu clock speed and ram speed
        // user can also input those
        float cpuClock = (float) 2.2;
        int ramSize = 8;


        //getting display driver factory
        AbstractFactory displayDriverFactory = FactoryProducer.getFactory("display");

        //displayDriver resolution  based on CPU and RAM
        DisplayDriver displayDriver;
        if(ramSize>=8 & cpuClock>=2)
            displayDriver = displayDriverFactory.getDisplayDriver("high");
        else
            displayDriver = displayDriverFactory.getDisplayDriver("low");



        // getting print driver factory
        AbstractFactory printDriverFactory = FactoryProducer.getFactory("print");

        //printDriver resolution  based on CPU and RAM
        PrintDriver printDriver;
        if(ramSize>=4 & cpuClock>=1.8)
            printDriver = printDriverFactory.getPrintDriver("high");
        else
            printDriver = printDriverFactory.getPrintDriver("low");


        // output both display and print driver resolutions
        displayDriver.display();
        printDriver.print();

    }

}
