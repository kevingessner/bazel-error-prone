package eptest;

class ErrorProneExample {
  private static long foo(byte a) {
    return a << 16 | a << 8 & 0xFF00L;
  }
}
