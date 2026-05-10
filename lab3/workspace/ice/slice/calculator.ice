
#ifndef CALC_ICE
#define CALC_ICE
[["java:package:sr.ice"]]
module Demo
{
  sequence<long> seqLong;

  enum operation { MIN, MAX, AVG };
  
  exception NoInput {};

  exception EmptyInput {
        string reason = "Input cannot be empty";
  };

  struct A
  {
    short a;
    long b;
    float c;
    string d;
  }

  interface Calc
  {
    long add(int a, int b);
    long subtract(int a, int b);
    double avg(seqLong numbers) throws EmptyInput;
    void op(A a1, short b1); //załóżmy, że to też jest operacja arytmetyczna ;)
  };

};

#endif
