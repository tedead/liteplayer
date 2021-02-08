package org.nibbler.zoe.liteplayer;
import android.content.Context;
import android.view.View;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class AdMob extends View {

	private AdView adView;
	
	private static final String AD_UNIT_ID = "a15094636fc8408";
	
	public AdMob(Context context) {
		
		super(context);

	}
	
	public View getAdMobView() {
		
	    adView = new AdView(getContext());
	     
	    adView.setAdUnitId(AD_UNIT_ID);
	     
	    adView.setAdSize(AdSize.BANNER);

        //When app is running look in LogCat for a string similar to that below, the device id should be different.
        //Use AdRequest.Builder.addTestDevice("CEB4D8EFEA9A99A10C5ACECC79D94B4B") to get test ads on this device.
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice("CEB4D8EFEA9A99A10C5ACECC79D94B4B").build();

        adView.loadAd(adRequest);
        
		return adView;
			
	}
	
	public void onResume() {

	    if (adView != null) {
	    	
	      adView.resume();
	      
	    }
	    
	  }

	  public void onPause() {
		  
	    if (adView != null) {
	    	
	      adView.pause();
	      
	    }

	  }

	  public void onDestroy() {

	    if (adView != null) {
	    	
	      adView.destroy();
	      
	    }
	    
	  }

}
