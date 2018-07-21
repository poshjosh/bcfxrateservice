# bcfxrateservice

API for accessing forex rate services in a fluent consistent way

#   Good to know
    
    Accesses forex rate from multiple sources hence will fall back if any once service is unavailable
    
    Easy to use

    Rates could be converted using instances of java.util.Locale 

    Light weight

    Uses a cache to prevent repeated calls to the backing forex rate service
    

# Example code

```java

import com.bc.fxrateservice.FxRate;
import com.bc.fxrateservice.FxRateService;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * @author Chinomso Bassey Ikwuagwu on Jul 21, 2018 4:34:23 PM
 */
public class ReadMe {

    public static void main(String... args) {
        
        FxRateService fxRateSvc = new DefaultFxRateService();
       
        FxRate rate = fxRateSvc.getRate(Locale.CHINESE, Locale.ITALIAN);
        
        System.out.println("Rate: " + rate.getRateOrDefault(-1f) + ", date: " + rate.getDate());
        
        rate = fxRateSvc.getRate("USD", "GBP");
        
        System.out.println("Rate: " + rate.getRateOrDefault(-1f) + ", date: " + rate.getDate());
        
        // You could configure update interval
        //
        
        final long updateInterval = TimeUnit.HOURS.toMillis(3);
        
        fxRateSvc = new DefaultFxRateService(
                new ServiceDescriptorImpl("My Forex Rate Service"),
                new FixerFxRateService(updateInterval), 
                new ECBFxRateService(updateInterval)
        );
    }
}
```