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

import java.util.Date;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ru.linedown.nefeslechat.R;
import ru.linedown.nefeslechat.ui.layuots.LastMessageLayout;
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
    Disposable innerDisposable;

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
                    if(chat.getType() == ChatTypeEnum.SINGLE){
                        if(chat.getUserType() == RoleEnum.PROFESSOR) {
                            chatType = LastMessageLayout.PREPOD;
                        }
                        else{
                            chatType = LastMessageLayout.STUDENT;
                        }
                    } else{
                        chatType = LastMessageLayout.GROUP;
                    }

                    boolean isRead = chat.getNotRead() > 0;
                    String lastMessageText = chat.getLastMessage() == null ? "" :
                            chat.getMessageFrom() + " " + chat.getLastMessage().getText();
                    Date lastMessageDate = chat.getLastMessage() == null ? null : chat.getLastMessage().getCreatedAt();
                    LastMessageAttributes lma = new LastMessageAttributes
                            (chat.getId(), lastMessageText, chat.getName(), chatType, isRead, lastMessageDate);

                    LastMessageLayout lml = new LastMessageLayout(getActivity(), lma);

                    lml.setOnClickListener(view -> {
                        final Bundle bundle = new Bundle();

                        Observable<String> innerObservable = Observable.fromCallable(() -> {
                            String chatTypeStr;
                            Log.d("Чат", chat.getType().toString());
                            if(chat.getType() == ChatTypeEnum.STUDGROUP) chatTypeStr = "Group";
                            else chatTypeStr = "Single";
                            bundle.putString("TitleToolBar", chat.getName());
                            Log.d("Имя чата: ", chat.getName());
                            bundle.putString("ChatId", String.valueOf(chat.getId()));
                            bundle.putString("ChatType", chatTypeStr);
                            if(chat.getType() == ChatTypeEnum.SINGLE) bundle.putString("UserId", String.valueOf(chat.getUserId()));

                            return "OK";
                        });

                        MyCallback<String> mcInner = new MyCallback<>() {
                            @Override
                            public void onSuccess(String result) {
                                Log.d("observableInner", "Ответ: " + result);

                                NavController navController = Navigation.findNavController(view);
                                navController.navigate(R.id.action_global_to_nav_chat, bundle);
                            }

                            @Override
                            public void onError(String errorMessage) {
                                Log.d("observableInner", "Ошибка получения данных: " + errorMessage);
                            }
                        };

                        innerDisposable = innerObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                                .subscribe(mcInner::onSuccess, error -> mcInner.onError(error.getMessage()));

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

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(disposable != null && !disposable.isDisposed()) disposable.dispose();
        if(innerDisposable != null && !innerDisposable.isDisposed()) innerDisposable.dispose();
        binding = null;
    }

}