// issue #47 

//template allSameSupport(T...){
//   enum bool allSameSupport = is( ((T[0]).type) == ((T[1]).type) )  ;
//}

auto allSameSupport = is( (T[0]) == bool )  ;

static if( is( (T[0]) == bool ) {
}