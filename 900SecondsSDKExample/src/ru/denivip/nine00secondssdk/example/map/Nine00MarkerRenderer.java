package ru.denivip.nine00secondssdk.example.map;

import ru.denivip.nine00secondssdk.example.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

public class Nine00MarkerRenderer extends DefaultClusterRenderer<Nine00ClusterMarker> {
    private final IconGenerator mClusterIconGenerator;
    private final ImageView mClusterImageView;
	private TextView mClusterText;
	private View multiProfile;
	private ImageView mClusterGradientBackgroundView;

    public Nine00MarkerRenderer(Context context, GoogleMap map, ClusterManager<Nine00ClusterMarker> clusterManager) {
        super(context, map, clusterManager);
        mClusterIconGenerator = new IconGenerator(context);
        multiProfile = ((Activity) context).getLayoutInflater().inflate(R.layout.map_marker, null);
        mClusterImageView = (ImageView) multiProfile.findViewById(R.id.image);
        mClusterGradientBackgroundView = (ImageView) multiProfile.findViewById(R.id.gradient_background);
        mClusterText = (TextView) multiProfile.findViewById(R.id.text);

        mClusterIconGenerator.setContentView(multiProfile);
        mClusterIconGenerator.setBackground(null);
    }

    @Override
    protected void onBeforeClusterItemRendered(Nine00ClusterMarker marker, MarkerOptions markerOptions) {
        mClusterImageView.setImageResource(R.drawable.map_pin_play);
        mClusterText.setVisibility(View.GONE);
        mClusterGradientBackgroundView.setAlpha(marker.getStreamData().getRelativePopularity()/100f * 0.5f + 0.5f);
        mClusterGradientBackgroundView.setScaleX(1);
        mClusterGradientBackgroundView.setScaleY(1);
        ColorMatrix cm = new ColorMatrix();
        float brightness = 100f - marker.getStreamData().getRelativeCreated()/100f * 100;
        cm.set(new float[] { 1, 0, 0, 0, brightness, 
        					 0, 1, 0, 0, brightness, 
        					 0, 0, 1, 0, brightness, 
        					 0, 0, 0, 1, 0 });
        ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(cm);
        mClusterImageView.setColorFilter(colorFilter);
        
        Bitmap icon = mClusterIconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<Nine00ClusterMarker> cluster, MarkerOptions markerOptions) {
    	
        mClusterImageView.setImageResource(R.drawable.map_pin_num_bckg);
        mClusterText.setVisibility(View.VISIBLE);
        mClusterText.setText(String.valueOf(cluster.getSize()));
        int maxPopularity = 0;
        int maxCreated = 0;
        for (Nine00ClusterMarker marker : cluster.getItems()){ 
			maxPopularity = Math.max(maxPopularity, marker.getStreamData().getRelativePopularity());
			maxCreated = Math.max(maxCreated, marker.getStreamData().getRelativeCreated());
        }
        mClusterGradientBackgroundView.setAlpha(maxPopularity/100f * 0.5f + 0.5f);
        mClusterGradientBackgroundView.setScaleX(1.3f);
        mClusterGradientBackgroundView.setScaleY(1.3f);
        
        ColorMatrix cm = new ColorMatrix();
        float brightness = 100f - maxCreated/100f * 100f;
        cm.set(new float[] { 1, 0, 0, 0, brightness, 0,
                1, 0, 0, brightness, 0, 0, 1, 0,
                brightness, 0, 0, 0, 1, 0 });
        ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(cm);
        mClusterImageView.setColorFilter(colorFilter);
        
        Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        // Always render clusters.
        return cluster.getSize() > 1;
    }
}