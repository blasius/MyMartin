package rw.martinhardware.mymartin.ui.repairs;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.objectbox.Box;
import rw.martinhardware.mymartin.BaseActivity;
import rw.martinhardware.mymartin.MyApp;
import rw.martinhardware.mymartin.R;
import rw.martinhardware.mymartin.databinding.ActivityCreateRepairRequestBinding;
import rw.martinhardware.mymartin.databinding.ItemAddedPhotoBinding;
import rw.martinhardware.mymartin.databinding.ItemLineItemBinding;
import rw.martinhardware.mymartin.entities.User;
import rw.martinhardware.mymartin.entities.User_;
import rw.martinhardware.mymartin.models.RepairVehicle;
import rw.martinhardware.mymartin.network.ApiConfig;
import rw.martinhardware.mymartin.network.VolleySingleton;

public class CreateRepairRequestActivity extends BaseActivity {

    private static final int REQ_LOCATION = 100;
    private static final int REQ_CAMERA_PERM = 101;
    private static final int REQ_IMAGE_CAPTURE = 102;

    private ActivityCreateRepairRequestBinding binding;
    private VolleySingleton volley;
    private String token;

    private final List<RepairVehicle> vehicles = new ArrayList<>();
    private final List<View> itemRows = new ArrayList<>();
    private final List<Uri> photoUris = new ArrayList<>();
    private Uri pendingPhotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateRepairRequestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        volley = VolleySingleton.getInstance(this);
        token = getAuthToken();

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        binding.spinnerType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                new String[]{"mechanical", "electrical", "body", "tire", "brake", "other"}));
        binding.spinnerPriority.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                new String[]{"low", "medium", "high", "critical"}));

        binding.btnAddItem.setOnClickListener(v -> addItemRow());
        binding.btnAddPhoto.setOnClickListener(v -> requestCamera());
        binding.btnSubmit.setOnClickListener(v -> onSubmitClicked());

        addItemRow();
        loadVehicles();
    }

    private void loadVehicles() {
        binding.btnSubmit.setEnabled(false);
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, ApiConfig.REPAIR_VEHICLES, null,
                response -> {
                    vehicles.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject j = response.getJSONObject(i);
                            RepairVehicle v = new RepairVehicle();
                            v.setId(j.optInt("id"));
                            v.setPlateNumber(j.optString("plate_number"));
                            v.setStatus(j.optString("status"));
                            vehicles.add(v);
                        } catch (JSONException ignored) {}
                    }
                    populateVehicleSpinner();
                },
                error -> Toast.makeText(this, "Failed to load vehicles", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> h = new HashMap<>();
                h.put("Accept", "application/json");
                if (token != null) h.put("Authorization", "Bearer " + token);
                return h;
            }
        };
        volley.addToRequestQueue(request);
    }

    private void populateVehicleSpinner() {
        List<String> labels = new ArrayList<>();
        for (RepairVehicle v : vehicles) {
            labels.add(v.getPlateNumber() + "  (" + v.getStatus() + ")");
        }
        if (labels.isEmpty()) {
            labels.add("No vehicles assigned");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, labels);
        binding.spinnerVehicle.setAdapter(adapter);
        binding.btnSubmit.setEnabled(true);
    }

    private void addItemRow() {
        ItemLineItemBinding rowBinding = ItemLineItemBinding.inflate(getLayoutInflater());
        rowBinding.btnRemoveItem.setOnClickListener(v -> {
            itemRows.remove(rowBinding.getRoot());
            binding.llItems.removeView(rowBinding.getRoot());
        });
        itemRows.add(rowBinding.getRoot());
        binding.llItems.addView(rowBinding.getRoot());
        binding.tvItemsHint.setVisibility(View.GONE);
    }

    private void requestCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQ_CAMERA_PERM);
            return;
        }
        launchCamera();
    }

    private void launchCamera() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePicture.resolveActivity(getPackageManager()) == null) {
            Toast.makeText(this, "No camera app available", Toast.LENGTH_SHORT).show();
            return;
        }
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(this, "Could not create photo file", Toast.LENGTH_SHORT).show();
            return;
        }
        pendingPhotoUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photoFile);
        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, pendingPhotoUri);
        startActivityForResult(takePicture, REQ_IMAGE_CAPTURE);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ROOT).format(new Date());
        File dir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "repairs");
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, "RR_" + timeStamp + ".jpg");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_IMAGE_CAPTURE && resultCode == RESULT_OK && pendingPhotoUri != null) {
            addPhotoView(pendingPhotoUri);
            pendingPhotoUri = null;
        }
    }

    private void addPhotoView(Uri uri) {
        ItemAddedPhotoBinding photoBinding = ItemAddedPhotoBinding.inflate(getLayoutInflater());
        photoBinding.ivPhoto.setImageURI(uri);
        photoBinding.btnRemovePhoto.setOnClickListener(v -> {
            photoUris.remove(uri);
            binding.llPhotos.removeView(photoBinding.getRoot());
        });
        photoUris.add(uri);
        binding.llPhotos.addView(photoBinding.getRoot());
        binding.tvPhotosHint.setVisibility(View.GONE);
    }

    private void onSubmitClicked() {
        String description = binding.etDescription.getText().toString().trim();
        if (description.isEmpty()) {
            Toast.makeText(this, "Description is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (vehicles.isEmpty() || binding.spinnerVehicle.getSelectedItemPosition() < 0) {
            Toast.makeText(this, "No vehicle selected", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQ_LOCATION);
            return;
        }
        getLocationAndSubmit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_CAMERA_PERM) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchCamera();
            } else {
                Toast.makeText(this, "Camera permission needed to add photos", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQ_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocationAndSubmit();
            } else {
                Toast.makeText(this, "Location permission needed to submit from the yard", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getLocationAndSubmit() {
        binding.btnSubmit.setEnabled(false);
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location last = null;
        try {
            if (lm != null) last = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (last == null && lm != null) last = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        } catch (SecurityException ignored) {}

        if (last != null) {
            submitRequest(last.getLatitude(), last.getLongitude());
            return;
        }

        if (lm == null) {
            submitRequest(Double.NaN, Double.NaN);
            return;
        }

        try {
            lm.requestSingleUpdate(LocationManager.GPS_PROVIDER, location -> {
                if (location != null) {
                    submitRequest(location.getLatitude(), location.getLongitude());
                } else {
                    submitRequest(Double.NaN, Double.NaN);
                }
            }, Looper.getMainLooper());
        } catch (SecurityException e) {
            submitRequest(Double.NaN, Double.NaN);
        }
    }

    private void submitRequest(double latitude, double longitude) {
        JSONObject body = new JSONObject();
        try {
            RepairVehicle vehicle = vehicles.get(binding.spinnerVehicle.getSelectedItemPosition());
            body.put("vehicle_id", vehicle.getId());
            body.put("type", binding.spinnerType.getSelectedItem().toString());
            body.put("priority", binding.spinnerPriority.getSelectedItem().toString());
            body.put("description", binding.etDescription.getText().toString().trim());
            if (!Double.isNaN(latitude)) body.put("latitude", latitude);
            if (!Double.isNaN(longitude)) body.put("longitude", longitude);

            JSONArray items = new JSONArray();
            for (View row : itemRows) {
                ItemLineItemBinding rb = ItemLineItemBinding.bind(row);
                String desc = rb.etItemDesc.getText().toString().trim();
                if (desc.isEmpty()) continue;
                JSONObject item = new JSONObject();
                item.put("description", desc);
                double qty = parseDoubleSafe(rb.etItemQty.getText().toString(), 1.0);
                double price = parseDoubleSafe(rb.etItemPrice.getText().toString(), 0.0);
                item.put("estimated_quantity", qty);
                item.put("estimated_unit_price", price);
                items.put(item);
            }
            if (items.length() > 0) body.put("items", items);
        } catch (JSONException e) {
            Toast.makeText(this, "Could not build request", Toast.LENGTH_SHORT).show();
            binding.btnSubmit.setEnabled(true);
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, ApiConfig.REPAIR_REQUESTS, body,
                response -> {
                    Toast.makeText(this, "Repair request created", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> {
                    binding.btnSubmit.setEnabled(true);
                    String msg = "Failed to create repair request";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        try {
                            String json = new String(error.networkResponse.data, "UTF-8");
                            JSONObject err = new JSONObject(json);
                            if (err.has("message")) msg = err.getString("message");
                            if (err.has("errors")) {
                                JSONObject errors = err.getJSONObject("errors");
                                if (errors.length() > 0) {
                                    String key = errors.names().getString(0);
                                    JSONArray arr = errors.getJSONArray(key);
                                    if (arr.length() > 0) msg = arr.optString(0);
                                }
                            }
                        } catch (Exception ignored) {}
                    }
                    if (error.networkResponse != null && error.networkResponse.statusCode == 422) {
                        showServerError("Not inside the yard?", msg);
                    } else {
                        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> h = new HashMap<>();
                h.put("Content-Type", "application/json");
                h.put("Accept", "application/json");
                if (token != null) h.put("Authorization", "Bearer " + token);
                return h;
            }
        };
        volley.addToRequestQueue(request);
    }

    private double parseDoubleSafe(String s, double fallback) {
        try {
            if (s == null || s.trim().isEmpty()) return fallback;
            return Double.parseDouble(s.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private void showServerError(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private String getAuthToken() {
        try {
            Box<User> box = ((MyApp) getApplication()).getBoxStore().boxFor(User.class);
            User u = box.query().equal(User_.isActive, true).build().findFirst();
            return u != null ? u.getToken() : null;
        } catch (Exception e) {
            return null;
        }
    }
}
