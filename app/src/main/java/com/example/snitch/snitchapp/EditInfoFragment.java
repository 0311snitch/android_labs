package com.example.snitch.snitchapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.snitch.snitchapp.models.User;
import com.example.snitch.snitchapp.storage.AvatarRepository;
import com.example.snitch.snitchapp.storage.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.io.File;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import static android.app.Activity.RESULT_OK;



/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditInfoFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "EditInfoFragment";
    private static final int RC_PICK_IMAGE_REQUEST = 1234;
    private static final int RC_TAKE_PHOTO_REQUEST = 4321;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private Button saveprofile;
    private String mParam2;
    NavController navController;

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private EditText nameEditText;
    private EditText surnameEditText;
    private EditText numberEditText;
    private EditText emailEditText;
    private ImageView avatarView;
    private User user;
    private Uri selectedAvatarUri = null;
    private Bitmap takenAvatarBitmap = null;

    //private OnAvatarImageClickListener mListener;

    private AvatarRepository.OnAvatarDownloadedListener onAvatarDownloadedListener;

    private OnFragmentInteractionListener mListener;

    public EditInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditInfoFragment newInstance(String param1, String param2) {
        EditInfoFragment fragment = new EditInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("name", nameEditText.getText().toString());
        outState.putString("surname", surnameEditText.getText().toString());
        outState.putString("email", emailEditText.getText().toString());
        outState.putString("phone", numberEditText.getText().toString());
        outState.putString("selectedAvatarUri", selectedAvatarUri == null? null : selectedAvatarUri.toString());
        outState.putParcelable("takenAvatarBitmap", takenAvatarBitmap);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_info, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        saveprofile = view.findViewById(R.id.save_button);
        saveprofile.setOnClickListener(this);
        nameEditText = view.findViewById(R.id.edit_name);
        surnameEditText = view.findViewById(R.id.edit_surname);
        numberEditText = view.findViewById(R.id.edit_phone);
        emailEditText = view.findViewById(R.id.edit_email);
        avatarView = view.findViewById(R.id.edit_avatar);

        user = UserRepository.getInstance().getUser();
        if (user == null) {
            user = new User();
        }

        selectedAvatarUri = null;

        if (savedInstanceState == null) {
            nameEditText.setText(user.getName());
            surnameEditText.setText(user.getSurname());
            emailEditText.setText(user.getEmail());
            numberEditText.setText(user.getPhone());

        } else {
            nameEditText.setText(savedInstanceState.getString("name"));
            surnameEditText.setText(savedInstanceState.getString("surname"));
            emailEditText.setText(savedInstanceState.getString("email"));
            numberEditText.setText(savedInstanceState.getString("phone"));
            String avatarUri = savedInstanceState.getString("selectedAvatarUri");
            takenAvatarBitmap = savedInstanceState.getParcelable("takenAvatarBitmap");
            if (avatarUri != null) {
                selectedAvatarUri = Uri.parse(avatarUri);
            }
        }
        if (selectedAvatarUri != null) {
            avatarView.setImageURI(selectedAvatarUri);
        }else if (takenAvatarBitmap != null) {
            avatarView.setImageBitmap(takenAvatarBitmap);
        } else {
            setFileAsAvatar(AvatarRepository.getInstance().getAvatarFile());
        }
        onAvatarDownloadedListener = new AvatarRepository.OnAvatarDownloadedListener() {
            @Override
            public void onAvatarDownloaded(File avatar) {
                setFileAsAvatar(avatar);
            }

            @Override
            public void onAvatarDownloadFailure(Exception e) {
                Toast.makeText(getContext(), "Error downloading", Toast.LENGTH_SHORT).show();
            }
        };
        AvatarRepository.getInstance().addOnAvatarDownloadedListener(onAvatarDownloadedListener);

        avatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAvatarPhotoSourceSelectionDialog();
            }
        });

    }

    private void setFileAsAvatar(File file) {
        if (file == null)
            return;
        Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        avatarView.setImageBitmap(myBitmap);
    }

    private void showAvatarPhotoSourceSelectionDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.choose_variant);
        dialog.setTitle("Choose avatar file");
        Button makePhotoButton = dialog.findViewById(R.id.from_camera);
        makePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePictureIntent, RC_TAKE_PHOTO_REQUEST);
                dialog.hide();
            }
        });
        Button chooseExistingButton = dialog.findViewById(R.id.from_gallery);
        chooseExistingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select picture"), RC_PICK_IMAGE_REQUEST);
                dialog.hide();
            }
        });
        dialog.show();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onClick(View view) {
        navController = Navigation.findNavController(Objects.requireNonNull(getActivity()), R.id.nav_host_fragment);
        if(view.getId() == R.id.save_button) {
            User user = new User();
            user.setName(nameEditText.getText().toString());
            user.setSurname(surnameEditText.getText().toString());
            user.setPhone(numberEditText.getText().toString());
            user.setEmail(emailEditText.getText().toString());
            UserRepository.getInstance().setUser(user);
            if (selectedAvatarUri != null) {
                Log.e(TAG, "NOT NULL");
                AvatarRepository.getInstance().setAvatar(selectedAvatarUri);
            } else if (takenAvatarBitmap != null) {
                Log.e(TAG, "NULL NULL NULL");
                AvatarRepository.getInstance().setAvatar(takenAvatarBitmap);
            }
            else
            {
                Log.e(TAG,"selectedAvatarUri - NULL");
            }
            navController.navigate(R.id.profile_frag);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RC_PICK_IMAGE_REQUEST:
                if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                    selectedAvatarUri = data.getData();
                    avatarView.setImageURI(selectedAvatarUri);
                    takenAvatarBitmap = null;
                }
                break;
            case RC_TAKE_PHOTO_REQUEST:
                if (resultCode == RESULT_OK){
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    avatarView.setImageBitmap(imageBitmap);
                    takenAvatarBitmap = imageBitmap;
                    selectedAvatarUri = null;
                }
        }
    }
}
