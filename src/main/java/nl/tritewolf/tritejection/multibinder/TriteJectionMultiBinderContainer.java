package nl.tritewolf.tritejection.multibinder;

import lombok.Getter;
import nl.tritewolf.tritejection.exceptions.TriteJectionNoMultiBinderException;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TriteJectionMultiBinderContainer {

    private final List<TriteJectionMultiBinder> triteJectionMultiBinders = new ArrayList<>();

    public TriteJectionMultiBinder getTriteJectionMultiBinder(Class<?> triteJectionMultiBinder) {
        return triteJectionMultiBinders.stream().filter(binder -> binder.getMultiBindingClass().equals(triteJectionMultiBinder))
                .findFirst()
                .orElseThrow(() -> new TriteJectionNoMultiBinderException(triteJectionMultiBinder.getSimpleName()));
    }

    public void addTriteJectionMultiBinder(TriteJectionMultiBinder triteJectionMultiBinder){
        this.triteJectionMultiBinders.add(triteJectionMultiBinder);
    }

}
