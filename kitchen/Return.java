package kitchen;

/**
 *  called serve in the language (Grandma's kitchen), but this is a class for return statements
 */
class Serve extends RuntimeException {
  final Object value;

  Serve(Object value) {
    super(null, null, false, false);
    this.value = value;
  }
}
