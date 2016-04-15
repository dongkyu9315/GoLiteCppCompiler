package abc;

type point struct {
	x []int;
}

func main() {
	var i int;
	i, j := 3, 5
	var x []int
	x = append(x, 3);
	var asdf point
	asdf.x = append(x, 6);
	var y []int
	y = append(x, 5);
	z := append(x, 10);
	x, afa := append(x, 111), 3;
}