public abstract class AbstractFactory {
    abstract DisplayDriver getDisplayDriver(String displayType);
    abstract PrintDriver getPrintDriver(String printType);
}
