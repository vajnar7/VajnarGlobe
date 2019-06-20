package si.vajnartech.vajnarglobe;

import si.vajnartech.vajnarglobe.math.R2Double;

public interface Transformator
{
  int normalize(double a1, int spoo);
  R2Double transform(R2Double p, boolean norm);
}
