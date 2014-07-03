module sampledefs_refs;

import sampledefs;


/*sampledefs.ImportSelectiveAlias*/ImportSelectiveAlias xxx;
/*sampledefs.ImportAliasingDefUnit*/ImportAliasingDefUnit xxx;

static import /*sampledefs.pack.sample*/pack.sample;

int xxx = /*sampledefs.variable*/variable;


int xxx = Class./*sampledefs.Class.fieldA*/fieldA;
int xxx = Class./*sampledefs.Class.methodB*/methodB();
auto xxx = Class./*sampledefs.Class.methodB*/methodB;

/*sampledefs.Class*/Class xxx;
/*sampledefs.Interface*/Interface xxx;
/*sampledefs.Struct*/Struct xxx;
/*sampledefs.Union*/Union xxx;

/*sampledefs.Enum*/Enum xxx;
int xxx = Enum./*sampledefs.EnumMemberA*/EnumMemberA;
int xxx = Enum./*sampledefs.EnumMemberB*/EnumMemberB;

/*sampledefs.Typedef*/Typedef xxx;
/*sampledefs.Alias*/Alias xxx;

auto xxx = Template./*sampledefs.TypeParam*/TypeParam;
auto xxx = Template./*sampledefs.ValueParam*/ValueParam;
auto xxx = Template./*sampledefs.AliasParam*/AliasParam;
auto xxx = Template./*sampledefs.TupleParam*/TupleParam;


Template!()./*sampledefs.TplNestedClass*/TplNestedClass xxx;

auto xxx = Template!().TplNestedClass./*sampledefs.func*/func;
auto xxx = Template!().TplNestedClass.func./*sampledefs.parameter*/parameter;

