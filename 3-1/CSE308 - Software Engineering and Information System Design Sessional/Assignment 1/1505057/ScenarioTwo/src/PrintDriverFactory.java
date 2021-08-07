public class PrintDriverFactory extends AbstractFactory{
    @Override
    DisplayDriver getDisplayDriver(String displayType) {
        return null;
    }

    @Override
    PrintDriver getPrintDriver(String printType) {
        if(printType==null)
            return null;
        if(printType.equalsIgnoreCase("low"))
            return new LRPD();
        else if(printType.equalsIgnoreCase("high"))
            return new HRPD();
        return null;
    }
}
