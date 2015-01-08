module pack.import_self;

import pack.import_self;

int xpto;

auto _ = pack.import_self/*M*/;
auto _ = xpto/*M2*/;

void func() {
	import pack.import_self;
	
	auto _ = xpto/*M3*/;
}