// TODO: new mixin template syntax

/+
mixin template MyTemplate(T, U) {
}

public:

private mixin template _workaround4424() {
        @disable void opAssign(typeof(this) );
}


mixin(injectNamedFields());

mixin MyTemplate!(T) m;
mixin MyTemplate!(T);
mixin MyTemplate;

void func() {
	mixin MyTemplate!(T) m;
	mixin MyTemplate!(T);
	mixin MyTemplate;
}

+/