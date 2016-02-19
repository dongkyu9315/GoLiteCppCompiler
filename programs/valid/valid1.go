package main

func main(){
	var x = uint64(5)
	print("the result is:")
	print(fac(x))
}

func fac(n uint64) uint64 {
	if n > 0{
		return n * fac(n-1)
	}
	return uint64(1);
}