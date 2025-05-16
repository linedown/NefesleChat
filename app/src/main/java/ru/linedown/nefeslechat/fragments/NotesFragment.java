package ru.linedown.nefeslechat.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ru.linedown.nefeslechat.databinding.FragmentNotesBinding;
import ru.linedown.nefeslechat.entity.EditMessagePayload;
import ru.linedown.nefeslechat.entity.MessageAllInfoDTO;
import ru.linedown.nefeslechat.entity.MessageSendDTO;
import ru.linedown.nefeslechat.entity.WebSocketDTO;
import ru.linedown.nefeslechat.enums.MessageTypeEnum;
import ru.linedown.nefeslechat.interfaces.ChoseActionListener;
import ru.linedown.nefeslechat.interfaces.DeleteMessageActionListener;
import ru.linedown.nefeslechat.interfaces.EditMessageActionListener;
import ru.linedown.nefeslechat.interfaces.MyCallback;
import ru.linedown.nefeslechat.layuots.ChoseActionDialogFragment;
import ru.linedown.nefeslechat.layuots.ConfirmDeleteMessageDialogFragment;
import ru.linedown.nefeslechat.layuots.EditMessageDialogFragment;
import ru.linedown.nefeslechat.layuots.NotesLayout;
import ru.linedown.nefeslechat.utils.OkHttpUtil;
import ru.linedown.nefeslechat.utils.WebSocketConnection;

public class NotesFragment extends Fragment implements ChoseActionListener, EditMessageActionListener, DeleteMessageActionListener {
    private FragmentNotesBinding binding;
    private LinearLayout onlyNoteLayout;
    int myId;
    int chatId;
    final int MS = 69;
    private String noteLayoutChooseText;
    private NotesLayout chooseLayout;
    Disposable disposable;
    ScrollView scrollViewInChat;
    Disposable deleteDisposable;
    Disposable loadMessageDisposable;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentNotesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ImageButton createButton = binding.addNoteButton;
        onlyNoteLayout = binding.onlyNotesLayout;

        myId = OkHttpUtil.getMyId();
        scrollViewInChat = binding.scrollViewInChat;

        WebSocketConnection.subscribeOnSendMessageEvent(message -> {
            MessageAllInfoDTO messageInChatDTO = new Gson().fromJson(message, MessageAllInfoDTO.class);
            addNotes(messageInChatDTO);
        });

        Observable<List<MessageAllInfoDTO>> observable = Observable.fromCallable(() -> {
            chatId = Integer.parseInt(OkHttpUtil.getIdInChatForProfile(myId));
            return OkHttpUtil.getMessagesInChat(chatId);
        });

        MyCallback<List<MessageAllInfoDTO>> myCallback = new MyCallback<>() {
            @Override
            public void onSuccess(List<MessageAllInfoDTO> result) {
                for(MessageAllInfoDTO message : result) addNotes(message);
                new Handler(Looper.getMainLooper()).postDelayed(() -> scrollViewInChat.fullScroll(View.FOCUS_DOWN), MS);
            }

            @Override
            public void onError(String errorMessage) {
                Log.d("MessageFragment", "Ошибка с получением сообщений в чате: " + errorMessage);
            }
        };

        loadMessageDisposable = observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(myCallback::onSuccess, error -> myCallback.onError(error.getMessage()));


        createButton.setOnClickListener(view -> {
            noteLayoutChooseText = "";
            chooseLayout = null;
            showEditActionDialog();
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        clearAllDialogFragment();

        Log.d("ChatFragment", "onDestroyView() вызван");
        clearAllDialogFragment();

        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
            Log.d("NotesFragment", "Disposable отписан");
        }
        if(loadMessageDisposable != null && !loadMessageDisposable.isDisposed()){
            loadMessageDisposable.dispose();
            Log.d("NotesFragment", "Disposable (messageDisposable) отписан");
        }
        if(deleteDisposable != null && !deleteDisposable.isDisposed()){
            deleteDisposable.dispose();
            Log.d("NotesFragment", "Disposable (deleteMessageDisposable) отписан");
        }
        WebSocketConnection.unSubscribeOnSendMessageEvent();
        binding = null;
        Log.d("NotesFragment", "binding обнулен");
    }

    private void addNotes(MessageAllInfoDTO messageAllInfoDTO){
        NotesLayout notesLayout = new NotesLayout(getContext(), messageAllInfoDTO);
        notesLayout.setOnClickListener(view -> {
            noteLayoutChooseText = messageAllInfoDTO.getMessage();
            chooseLayout = notesLayout;
            showChooseActionDialog();
        });
        Log.d("Заметка " + messageAllInfoDTO.getId(), messageAllInfoDTO.getMessage());
        onlyNoteLayout.addView(notesLayout);
        new Handler(Looper.getMainLooper()).postDelayed(() -> scrollViewInChat.fullScroll(View.FOCUS_DOWN), MS);
    }

    private void showChooseActionDialog(){
        ChoseActionDialogFragment choseActionDialogFragment = new ChoseActionDialogFragment(this);
        choseActionDialogFragment.show(getChildFragmentManager(), "ChooseDialog");
    }

    private void showEditActionDialog(){
        EditMessageDialogFragment editMessageDialogFragment = new EditMessageDialogFragment(this, noteLayoutChooseText);
        editMessageDialogFragment.show(getChildFragmentManager(), "EditDialog");
    }

    private void showDeleteActionDialog(){
        ConfirmDeleteMessageDialogFragment confirmExitDialogFragment = new ConfirmDeleteMessageDialogFragment(this);
        confirmExitDialogFragment.show(getChildFragmentManager(), "DeleteDialog");
    }

    @Override
    public void onDeleteAction() {
        showDeleteActionDialog();
    }

    @Override
    public void onEditAction() {
        showEditActionDialog();
    }

    @Override
    public void onCancelAction() {
        Toast.makeText(getContext(), "Действие по выбору отменено", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onApplyDelete() {
        int messageId = chooseLayout.getId();
        Observable<String> observable = Observable.fromCallable(() -> {
            WebSocketDTO webSocketDTO = new WebSocketDTO("deleteMessage", messageId);
            if (WebSocketConnection.isConnected()){
                    WebSocketConnection.send(OkHttpUtil.getUserUrl() + myId, webSocketDTO);
                return "Успешно удалено";
            }
            else Log.w("WebSocket", "Соединение WebSocket не активно. Сообщение не удалено.");

            return "Не получилось удалить";
        });

        deleteDisposable = observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                result -> {
                    Log.d("ConfirmDialog", "Результат: " + result);
                    onlyNoteLayout.removeView(chooseLayout);
                },
                error -> Log.e("Удаление: исключение", "Текст исключения: " + error.getMessage(), error));
        Toast.makeText(getContext(), "Действие по удалению выполнено", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onCancelDelete() {
        Toast.makeText(getContext(), "Действие по удалению отменено", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onApplyEdit(String newText) {
        final WebSocketDTO webSocketDTO;
        if(!EditMessageDialogFragment.isCreate){
            int messageId = chooseLayout.getId();
            webSocketDTO = new WebSocketDTO("editMessage", new EditMessagePayload(messageId, newText));
        }
        else webSocketDTO = new WebSocketDTO("sendMessage", new MessageSendDTO(MessageTypeEnum.TEXT, newText));
        Observable<String> observable = Observable.fromCallable(() -> {
            if (WebSocketConnection.isConnected()){
                WebSocketConnection.send(OkHttpUtil.getUserUrl() + myId, webSocketDTO);
                String operation = EditMessageDialogFragment.isCreate ? "создания" : "изменения";
                return "Успешно выполнена операция " + operation;
            }
            else Log.w("WebSocket", "Соединение WebSocket не активно. Операция не сделана.");

            return "Не получилось выполнить операцию";
        });

        deleteDisposable = observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                result -> {
                    Log.d("EditDialog", "Результат: " + result);
                    if(!EditMessageDialogFragment.isCreate) chooseLayout.setTextInMessage(newText);
                },
                error -> Log.e("Изменение: исключение", "Текст исключения: " + error.getMessage(), error));
    }

    @Override
    public void onCancelEdit() {
        Toast.makeText(getContext(), "Действие по изменению отменено", Toast.LENGTH_SHORT).show();
    }

    private void clearAllDialogFragment(){
        FragmentManager fragmentManager = getChildFragmentManager();
        List<Fragment> dialogFragments = fragmentManager.getFragments();
        for(Fragment fragment : dialogFragments){
            if(fragment instanceof DialogFragment dialogFragment){
                dialogFragment.dismiss();
            }
        }
    }
}