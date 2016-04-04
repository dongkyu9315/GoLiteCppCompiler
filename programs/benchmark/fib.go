package main

func main() {
	print(fib(100))
}

func fib(n int) int {
	if n == 0 || n == 1{
		return 1
	} else {
		return fib(n-1) + fib(n-2)
	}
}