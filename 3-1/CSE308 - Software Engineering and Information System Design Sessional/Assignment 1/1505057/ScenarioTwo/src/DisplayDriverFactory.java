public class DisplayDriverFactory extends AbstractFactory{

    @Override
    DisplayDriver getDisplayDriver(String displayType) {
        if(displayType==null)
            return null;
        if(displayType.equalsIgnoreCase("low"))
            return new LRDD();
        else if(displayType.equalsIgnoreCase("high"))
            return new HRDD();
        return null;
    }

    @Override
    PrintDriver getPrintDriver(String printType) {
        return null;
    }
}
