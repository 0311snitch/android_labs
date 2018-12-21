package com.example.snitch.snitchapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.app.ProgressDialog;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.snitch.snitchapp.models.User;
import com.example.snitch.snitchapp.storage.AvatarRepository;
import com.example.snitch.snitchapp.storage.OnProgressListener;
import com.example.snitch.snitchapp.storage.UserRepository;

import java.io.File;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BlankFragment2.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BlankFragment2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BlankFragment2 extends Fragment implements View.OnClickListener, OnProgressListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    TextView surnameTextView;
    TextView nameTextView;
    TextView emailTextView;
    TextView phoneTextView;
    private ProgressDialog progressDialog;

    ImageView avatar;
    private UserRepository.OnUserUpdatedListener onUserDMUpdatedListener;
    private AvatarRepository.OnAvatarDownloadedListener onAvatarDownloadedListener;


    // TODO: Rename and change types of parameters
    private String mParam1;
    NavController navController;
    private Button editprofile;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public BlankFragment2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment2.
     */
    // TODO: Rename and change types and number of parameters
    public static BlankFragment2 newInstance(String param1, String param2) {
        BlankFragment2 fragment = new BlankFragment2();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank_fragment2, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        editprofile = view.findViewById(R.id.editprofile_button);
        editprofile.setOnClickListener(this);
        surnameTextView = view.findViewById(R.id.take_surname);
        nameTextView = view.findViewById(R.id.view_name);
        emailTextView = view.findViewById(R.id.view_email);
        phoneTextView = view.findViewById(R.id.view_phone);
        avatar = view.findViewById(R.id.avatar_view);

        UserRepository rep = UserRepository.getInstance();
        setUI(rep.getUser());
        setFileAsAvatar(AvatarRepository.getInstance().getAvatarFile());
        onUserDMUpdatedListener = new UserRepository.OnUserUpdatedListener() {
            @Override
            public void OnUserUpdated(User user) {
                setUI(user);
            }
        };
        rep.addOnUserDMUpdatedListener(onUserDMUpdatedListener);

        onAvatarDownloadedListener = new AvatarRepository.OnAvatarDownloadedListener() {
            @Override
            public void onAvatarDownloaded(File avatar) {
                setFileAsAvatar(avatar);
            }

            @Override
            public void onAvatarDownloadFailure(Exception e) {
                Toast.makeText(getContext(), "Error downloading avatar", Toast.LENGTH_SHORT).show();
            }
        };
        AvatarRepository.getInstance().addOnProgressListener(this);
        AvatarRepository.getInstance().addOnAvatarDownloadedListener(onAvatarDownloadedListener);
    }

    private void setUI(User user) {
        if (user == null) {
            return;
        }
        nameTextView.setText(user.getName());
        surnameTextView.setText(user.getSurname());
        emailTextView.setText(user.getEmail());
        phoneTextView.setText(user.getPhone());
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
        AvatarRepository.getInstance().removeOnAvatarDownloadedListener(onAvatarDownloadedListener);
        UserRepository.getInstance().removeOnUserDMUpdatedListener(onUserDMUpdatedListener);
        AvatarRepository.getInstance().removeOnProgressListener(this);
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

    private void setFileAsAvatar(File file) {
        if (file == null)
            return;
        Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        avatar.setImageBitmap(myBitmap);
    }

    @Override
    public void onClick(View view) {
        navController = Navigation.findNavController(Objects.requireNonNull(getActivity()), R.id.nav_host_fragment);

        if(view.getId() == R.id.editprofile_button) {
            navController.navigate(R.id.edit_profile);
        }
    }

    @Override
    public void onProgressStarted() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
        }
        progressDialog.setMessage("Идёт загрузка");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    public void onProgressEnded() {
        if (progressDialog != null) {
            progressDialog.hide();
        }
    }
}
