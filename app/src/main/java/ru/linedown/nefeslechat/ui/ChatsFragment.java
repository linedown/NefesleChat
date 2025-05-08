package ru.linedown.nefeslechat.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import ru.linedown.nefeslechat.R;
import ru.linedown.nefeslechat.classes.LastMessageLayout;
import ru.linedown.nefeslechat.databinding.FragmentChatsBinding;

public class ChatsFragment extends Fragment {

    private FragmentChatsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentChatsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        LastMessageLayout lml = new LastMessageLayout(getActivity(), LastMessageLayout.PREPOD);

        lml.setChatName("Олег Проурзин");
        lml.setMessage("Я рад, что у вас всё получается");

        LinearLayout chatLayout = binding.chatsLayout;

        LastMessageLayout lml2 = new LastMessageLayout(getActivity(), LastMessageLayout.STUDENT);

        lml.setChatName("Роман Слесарев");
        lml.setMessage("Ура заработало!");

        chatLayout.addView(lml);
        chatLayout.addView(lml2);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}