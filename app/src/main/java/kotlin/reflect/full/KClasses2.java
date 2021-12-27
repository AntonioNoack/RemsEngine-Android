package kotlin.reflect.full;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import kotlin.reflect.KCallable;
import kotlin.reflect.KClass;
import kotlin.reflect.KMutableProperty1;
import kotlin.reflect.KType;

public class KClasses2 {

    // somehow missing on Android :/
    public static <V> List<KClass<V>> getSuperclasses(KClass<V> clazz) {
        ArrayList<KClass<V>> list = new ArrayList<>();
        for (KType x : clazz.getSupertypes()) {
            list.add((KClass<V>) x.getClassifier());
        }
        return list;
    }

    /*@RequiresApi(api = Build.VERSION_CODES.N)
    public static <V, A, B> Collection<KMutableProperty1<A, B>> getMemberProperties(KClass<V> clazz) {
        ((KClassImpl<V>) clazz).data().allNonStaticMembers.filter { it.isNotExtension && it is KProperty1<*, *> } as Collection<KProperty1<T, *>>
        ArrayList<KMutableProperty1<A, B>> result = new ArrayList<>();
        for (KCallable<?> callable : clazz.getMembers()) {
            if (callable instanceof KMutableProperty1) {
                result.add((KMutableProperty1<A, B>) callable);
            }
        }
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static <V, A, B> Collection<KMutableProperty1<A, B>> getDeclaredMemberProperties(KClass<V> clazz) {
        ArrayList<KMutableProperty1<A, B>> result = new ArrayList<>();
        for (KCallable<?> callable : clazz.getMembers()) {
            if (callable instanceof KMutableProperty1) {
                result.add((KMutableProperty1<A, B>) callable);
            }
        }
        return result;
    }*/

}
