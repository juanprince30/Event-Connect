package bf.uv.eventconnect;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Date;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.Executors;

public class AddActivity extends AppCompatActivity {
    private EditText Date;
    private Button btnChooseImage;
    private ImageView imagePreview;
    private Uri imageUri;
    Spinner spinnerCategory;
    String selectedCategory = "";
    private Button btnChooseLocation;
    private double selectedLatitude = 0.0;
    private double selectedLongitude = 0.0;


    private ActivityResultLauncher<String> galleryLauncher;
    private ActivityResultLauncher<Uri> cameraLauncher;
    private ActivityResultLauncher<Intent> mapLauncher;


    private static final int REQUEST_CAMERA_PERMISSION = 200; // ✅ ID unique

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add);

        EditText editorganisateur= findViewById(R.id.editOrganisateur);
        EditText edittitre=findViewById(R.id.editTitre);
        EditText editdescription= findViewById(R.id.editDescription);
        EditText editlieu=findViewById(R.id.editLieu);

        Button save= findViewById(R.id.btnSave);
        Date = findViewById(R.id.editDate);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        imagePreview = findViewById(R.id.imagePreview);

        edittitre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Vérifie si le champ est vide
                String titre = edittitre.getText().toString().trim();
                save.setEnabled(!titre.isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        Date.setOnClickListener(v -> showDatePicker());
        initActivityResultLaunchers();
        btnChooseImage.setOnClickListener(v -> showImagePickerDialog());


        spinnerCategory = findViewById(R.id.spinnerCategory);

        // Liste des catégories
        String[] categories = {"Concert", "Sport", "Conférence", "Festival", "Autres", "Concours"};

        // Adapter (permet d'afficher la liste dans le Spinner)
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Associer l’adapter au Spinner
        spinnerCategory.setAdapter(adapter);

        // Gérer la sélection
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = categories[position];
                //Toast.makeText(AddActivity.this, "Catégorie : " + selectedCategory, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategory = "";
            }
        });

        btnChooseLocation = findViewById(R.id.btnChooseLocation);

        btnChooseLocation.setOnClickListener(v->{
            Log.d("TAG","Boutton clicker dans lapp pour migrer vers la Maps Activity");
            Intent intent=new Intent(AddActivity.this, GetMapsActivity.class);
            mapLauncher.launch(intent);
        });

        mapLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            selectedLatitude = data.getDoubleExtra("latitude", 0.0);
                            selectedLongitude = data.getDoubleExtra("longitude", 0.0);

                            Log.d("AddActivity", "Latitude: " + selectedLatitude + ", Longitude: " + selectedLongitude);

                            Toast.makeText(this, "Position choisie: " +
                                            selectedLatitude + ", " + selectedLongitude,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );

        save.setOnClickListener(v->{

            String titre = edittitre.getText().toString().trim();
            String organisateur = editorganisateur.getText().toString().trim();
            String description = editdescription.getText().toString().trim();
            String lieu = editlieu.getText().toString().trim();
            String date = Date.getText().toString().trim();
            String category = selectedCategory;

            String imagePath = null;
            if (imageUri != null) {
                imagePath = imageUri.getPath();
            }


            if (titre.isEmpty() || organisateur.isEmpty() || description.isEmpty() || lieu.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs obligatoires", Toast.LENGTH_SHORT).show();
                return;
            }

            // ⚡ Appel à ta fonction pour insérer dans Room
            saveToRoom(
                    titre,
                    organisateur,
                    description,
                    lieu,
                    imagePath,
                    category,
                    selectedLatitude,
                    selectedLongitude,
                    date
            );
        });



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    calendar.set(selectedYear, selectedMonth, selectedDay);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    String dateString = sdf.format(calendar.getTime());
                    Date.setText(dateString);
                },
                year, month, day
        );

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void initActivityResultLaunchers() {

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        String copiedPath = copyImageToInternalStorage(uri);
                        if (copiedPath != null) {
                            imageUri = Uri.fromFile(new File(copiedPath));
                            imagePreview.setImageURI(imageUri);
                        }
                    }
                }
        );

        // Caméra
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (success && imageUri != null) {
                        String copiedPath = copyImageToInternalStorage(imageUri);
                        if (copiedPath != null) {
                            imageUri = Uri.fromFile(new File(copiedPath));
                            imagePreview.setImageURI(imageUri);
                        }
                    } else {
                        Log.d("TAG", "Prise de photo annulée ou échouée");
                    }
                }
        );

    }

    private void showImagePickerDialog() {
        String[] options = {"Galerie", "Caméra"};
        new AlertDialog.Builder(this)
                .setTitle("Ajouter une image")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        openGallery();
                    } else {

                        checkCameraPermissionAndOpen();
                    }
                })
                .show();
    }

    private void openGallery() {
        galleryLauncher.launch("image/*");
    }


    private void checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        } else {
            openCamera();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Permission caméra refusée", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        try {
            File photoFile = createImageFile();
            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(
                        this,
                        "bf.uv.eventconnect.provider", // ⚡ doit matcher le Manifest
                        photoFile
                );
                cameraLauncher.launch(imageUri);
            } else {
                Toast.makeText(this, "Impossible de créer le fichier image", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur lors de la création du fichier image", Toast.LENGTH_SHORT).show();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur FileProvider : vérifie ton authority", Toast.LENGTH_LONG).show();
        }
    }


    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private String getRealPathFromURI(Uri uri) {
        String path = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        try (Cursor cursor = getContentResolver().query(uri, proj, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                path = cursor.getString(columnIndex);
                Log.d("PATH",path);
            }
        } catch (Exception e) {
            Log.w("TAG", "getRealPathFromURI: impossible de récupérer le chemin réel", e);
        }
        return path;
    }

    private void saveToRoom(String titre, String organisateur, String description,
                            String lieu, String imagePath, String category,
                            double latitude, double longitude, String date) {

        AppDatabase db = DbProvider.get(this);
        Executors.newSingleThreadExecutor().execute(() -> {
            Event e = new Event();
            e.titre = titre;
            e.organisateur = organisateur;
            e.description = description;
            e.lieu = lieu;
            e.imagePath = imagePath;
            e.category = category;
            e.latitude = latitude;
            e.longitude = longitude;
            e.date = date;
            e.createdAt = System.currentTimeMillis(); // timestamp d’enregistrement

            long id = db.eventDao().insert(e);

            runOnUiThread(() -> {
                Toast.makeText(this, "Événement enregistré (id=" + id + ")", Toast.LENGTH_SHORT).show();
                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
                finish();
            });
        });
    }

    private String copyImageToInternalStorage(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            String fileName = "IMG_" + System.currentTimeMillis() + ".jpg";
            File file = new File(getFilesDir(), fileName);

            OutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();

            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
