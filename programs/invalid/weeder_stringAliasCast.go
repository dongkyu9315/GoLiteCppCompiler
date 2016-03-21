package main

var x int
var y int
type firstAlias string
type secondAlias firstAlias
func main(){
	switch x := 4; {  // missing switch expression means "true"
	case x < 0: return -x
	default: return x
	case x >3 : return secondAlias(x);
	}
}