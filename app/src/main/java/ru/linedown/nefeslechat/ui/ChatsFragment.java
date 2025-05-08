package ru.linedown.nefeslechat.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ru.linedown.nefeslechat.R;
import ru.linedown.nefeslechat.classes.LastMessageLayout;
import ru.linedown.nefeslechat.classes.OkHttpUtil;
import ru.linedown.nefeslechat.databinding.FragmentChatsBinding;
import ru.linedown.nefeslechat.entity.ChatDTO;
import ru.linedown.nefeslechat.entity.LastMessageAttributes;
import ru.linedown.nefeslechat.enums.ChatTypeEnum;
import ru.linedown.nefeslechat.enums.RoleEnum;
import ru.linedown.nefeslechat.interfaces.MyCallback;

public class ChatsFragment extends Fragment {

    private FragmentChatsBinding binding;
    Disposable disposable;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentChatsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        LinearLayout chatsLayout = binding.chatsLayout;

        Observable<List<ChatDTO>> observable = Observable.fromCallable(OkHttpUtil::getListChats);

        MyCallback<List<ChatDTO>> mc = new MyCallback<>() {
            @Override
            public void onSuccess(List<ChatDTO> result) {
                chatsLayout.removeAllViews();
                int chatType;

                for(ChatDTO chat : result){
                    if(chat.getType() == ChatTypeEnum.GROUP) chatType = LastMessageLayout.GROUP;
                    else if(chat.getType() == ChatTypeEnum.SINGLE){
                        if(chat.getUserType() == RoleEnum.STUDENT) chatType = LastMessageLayout.STUDENT;
                        else chatType = LastMessageLayout.PREPOD;
                    }
                    else chatType = 0;

                    LastMessageAttributes lma = new LastMessageAttributes(chat.getId(), chat.getLastMessage().getText(), chat.getName(), chatType);

                    LastMessageLayout lml = new LastMessageLayout(getActivity(), lma);

                    lml.setOnClickListener(view -> {
                        String chatTypeStr;
                        if(lml.getLma().getChatType() == LastMessageLayout.GROUP) chatTypeStr = "Group";
                        else chatTypeStr = "Single";
                        Bundle bundle = new Bundle();
                        bundle.putString("TitleToolBar", chat.getName());
                        bundle.putString("ChatId", String.valueOf(chat.getId()));
                        bundle.putString("ChatType", chatTypeStr);
                        NavController navController = Navigation.findNavController(view);
                        navController.navigate(R.id.action_global_to_nav_chat, null);
                    });

                    chatsLayout.addView(lml);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.d("ChatsFragment", "Ошибка при получении списка чатов: " + errorMessage);
            }
        };

        disposable = observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                mc::onSuccess, error -> mc.onError(error.getMessage()));


//        LastMessageLayout lml = new LastMessageLayout(getActivity(), LastMessageLayout.PREPOD);
//
//        lml.setChatName("Олег Проурзин");
//        lml.setMessage("Я рад, что у вас всё получается");
//
//        LinearLayout chatLayout = binding.chatsLayout;
//
//        LastMessageLayout lml2 = new LastMessageLayout(getActivity(), LastMessageLayout.STUDENT);
//
//        lml.setChatName("Роман Слесарев");
//        lml.setMessage("Ура заработало!");
//
//        chatLayout.addView(lml);
//        chatLayout.addView(lml2);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(disposable != null && !disposable.isDisposed()) disposable.dispose();
        binding = null;
    }

}