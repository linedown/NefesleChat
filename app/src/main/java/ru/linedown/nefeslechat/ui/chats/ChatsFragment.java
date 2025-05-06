package ru.linedown.nefeslechat.ui.chats;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import ru.linedown.nefeslechat.R;
import ru.linedown.nefeslechat.classes.LastMessageLayout;
import ru.linedown.nefeslechat.databinding.FragmentChatsBinding;

public class ChatsFragment extends Fragment {

    private FragmentChatsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentChatsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        LastMessageLayout lml = new LastMessageLayout(getActivity(), R.drawable.man, LastMessageLayout.STUDENT);
//
//        lml.setChatName("Роман Слесарев");
//        lml.setMessage("Ура заработало");
//
//        LinearLayout chatLayout = binding.chatsLayout;
//
//        LastMessageLayout lml2 = new LastMessageLayout(getActivity(), R.drawable.group, LastMessageLayout.GROUP);
//
//        chatLayout.addView(lml);
//        chatLayout.addView(lml2);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}