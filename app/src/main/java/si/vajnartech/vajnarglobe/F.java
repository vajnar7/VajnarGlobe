package si.vajnartech.vajnarglobe;


import android.util.LongSparseArray;

abstract class F<E> extends LongSparseArray<E>
{
  abstract E f(long t);
  abstract E f(String s);
  abstract E integral();
}

