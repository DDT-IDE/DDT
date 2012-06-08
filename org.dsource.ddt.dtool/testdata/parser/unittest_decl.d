
unittest {
}

unittest {
	static if(true) {
	}
}


/+__ INVALID _____________________+/
unittest func(;

/+__ UNSUPPORTED_INVALID _____________________+/
unittest func();

/+__ INVALID _____________________+/
unittest in {
} body {
}
