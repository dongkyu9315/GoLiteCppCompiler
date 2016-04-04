//this program is a simple neural network with gradient descent
//I've tried to make this as scalable as possible
package main

var random_seed = 123123

const input_dim = 2
const hidden_dim = 4
const output_dim = 2

const trainingsize = 4	//number of examples in the training set to allow for batch training

type traindata [trainingsize][input_dim]float64
type labeldata [trainingsize][output_dim]float64
type inputvec  [input_dim]float64
type outputvec [output_dim]float64

type nn struct {
	W1 [input_dim][hidden_dim]float64	//weight matrix of the hidden units
	W2 [hidden_dim][output_dim]float64	//weight matrix of the output units
	b1 [hidden_dim]float64		//bias vector of the hidden units
	b2 [output_dim]float64		//bias vector of the output units
}

func init_model(m nn) nn {
	for h := 0; h < hidden_dim; h++ {
		for i := 0; i < input_dim; i++ {
			m.W1[i][h] = random_custom() //if we have a math lib, we should add normalized by sqrt of input dim, but we dont, and im too lazy to write it myself
		}
		m.b1[h] = 0
	}
	for o := 0; o < output_dim; o++ {
		for h := 0; h < hidden_dim; h++ {
			m.W2[h][o] = random_custom()
		}
		m.b2[o] = 0
	}
	return m
}

func train(m nn, x traindata, y labeldata, learning_rate float64, pass int) nn {
	for p := 0; p < pass; p++ {

		//net1 = x*W1 + b1
		var net1 [trainingsize][hidden_dim]float64
		for t := 0; t < trainingsize; t++ {
			for i := 0; i < hidden_dim; i++ {
				net1[t][i] = 0
				for j := 0; j < input_dim; j++ {
					net1[t][i] += x[t][j] * m.W1[j][i]
				}
				net1[t][i] += m.b1[i];
			}
		}	

		//o1 = sigmoid(net1)
		var o1 [trainingsize][hidden_dim]float64
		for t := 0; t < trainingsize; t++ {
			for i := 0; i < hidden_dim; i++ {
				o1[t][i] = 1 / (1 + exp(-o1[t][i]))
			}
		}

		//net2 = o1*W2 + b2
		var net2 [trainingsize][output_dim]float64
		for t := 0; t < trainingsize; t++ {
			for o := 0; o < output_dim; o++ {
				net2[t][o] = 0
				for h := 0; h < hidden_dim; h++ {
					net2[t][o] += o1[t][h] * m.W2[h][o]
				}
				net2[t][o] += m.b2[o]
			}
		}

		//final output
		var probs [trainingsize][output_dim]float64
		for t := 0; t < trainingsize; t++ {
			probs[t] = softmax(net2[t])
		}

		//error value
		var error [trainingsize][output_dim]float64
		for t := 0; t < trainingsize; t++ {
			for o := 0; o < output_dim; o++ {
				error[t][o] = probs[t][o] - y[t][o]
			}
		}

		//dW2 = o1T * error
		var dW2 [hidden_dim][output_dim]float64
		for h := 0; h < hidden_dim; h++ {
			for o := 0; o < output_dim; o++ {
				dW2[h][o] = 0
				for t := 0; t < trainingsize; t++ {
					dW2[h][o] += o1[t][h] * error[t][o]
				}
				// dW2[h][o] += 0.01 * m.W2[h][o]
			}
		}

		//db2 = error sum over all training examples
		var db2 [output_dim]float64
		for o := 0; o < output_dim; o++ {
			db2[o] = 0.0
			for t := 0; t < trainingsize; t++ {
				db2[o] += error[t][o]
			}
		}

		//d1 = error * w2t mul o1 mul (1 - o1)
		var d1 [trainingsize][hidden_dim]float64
		for t := 0; t < trainingsize; t++ {
			for h := 0; h < hidden_dim; h++ {
				d1[t][h] = 0
				for o := 0; o < output_dim; o++ {
					d1[t][h] += error[t][o] * m.W2[h][o]
				}
				d1[t][h] += o1[t][h] * (1 - o1[t][h])
			}
		}

		//dw1 = inputT dot d1
		var dW1 [input_dim][hidden_dim]float64
		for i := 0; i < input_dim; i++ {
			for h := 0; h < hidden_dim; h++ {
				dW1[i][h] = 0
				for t := 0; t < trainingsize; t++ {
					dW1[i][h] += x[t][i] * d1[t][h]
				}
				// dW1[i][h] += 0.01 * m.W1[i][h]
			}
		}

		//db1 = d1 row sum
		var db1 [hidden_dim]float64
		for h := 0; h < hidden_dim; h++ {
			db1[h] = 0.0
			for t := 0; t < trainingsize; t++ {
				db1[h] += d1[t][h]
			}
		}

		//adding the gradients
		for h := 0; h < hidden_dim; h++ {
			for i := 0; i < input_dim; i++ {
				m.W1[i][h] += dW1[i][h] * -learning_rate
			}
			m.b1[h] += db1[h] * -learning_rate
		}
		for o := 0; o < output_dim; o++ {
			for h := 0; h < hidden_dim; h++ {
				m.W2[h][o] += dW2[h][o] * -learning_rate
			}
			m.b2[o] += db2[o] * -learning_rate
		}


	}
	return m
}

func predict(m nn, x [input_dim]float64) [output_dim]float64 {
	var net1 [hidden_dim]float64
	for i := 0; i < hidden_dim; i++ {
		net1[i] = 0.0
		for j := 0; j < input_dim; j++ {
			net1[i] += x[j] * m.W1[j][i]
		}
		net1[i] += m.b1[i];
	}

	//o1 = sigmoid(net1)
	var o1 [hidden_dim]float64
	for i := 0; i < hidden_dim; i++ {
		o1[i] = 1 / (1 + exp(-o1[i]))
	}

	//net2 = o1*W2 + b2
	var net2 [output_dim]float64
	for o := 0; o < output_dim; o++ {
		net2[o] = 0.0
		for h := 0; h < hidden_dim; h++ {
			net2[o] += o1[h] * m.W2[h][o]
		}
		net2[o] += m.b2[o]
	}

	//final output
	var probs outputvec
	probs = softmax(net2)

	return probs
}

//======================
//	Helper functions
//======================

func softmax (x outputvec) outputvec {
	var exp_score outputvec
	for o := 0; o < output_dim; o++ {
		exp_score[o] = exp(x[o])
	}
	var sum_exp float64 = 0.0
	for o := 0; o < output_dim; o++ {
		sum_exp += exp_score[o]
	}

	var softx outputvec
	for o := 0; o < output_dim; o++ {
		softx[o] = exp_score[o] / sum_exp
	}

	return softx
}

//approximate exp using maclaurin series up to 15 terms
func exp(n float64) float64 {
	var sum float64
	sum = 1 
	for i := 1; i < 15; i++ {
		var num float64 = 1.0
		for k := 0; k < i; k++ {
			num *= n
		}
		sum += num / float64(fac(i))
	}
	return sum
}
func fac(n int) int {
	if n == 1 || n == 0 {
		return 1
	} else {
		return n * fac(n-1)
	}
}

var _last_random = random_seed
var _random_a = 8191
var _random_b = 131071
var _random_mod = 523287
func random_custom() float64{
	r := (_random_a * _last_random + _random_b) % _random_mod
	_last_random = r
	return float64(r) / float64(_random_mod)
}

func main() {
	random_seed = 5331

	var m nn
	m = init_model(m)

	var input_data [trainingsize][input_dim]float64
	input_data[0][0] = 0
	input_data[0][1] = 0

	input_data[1][0] = 1
	input_data[1][1] = 0

	input_data[2][0] = 0
	input_data[2][1] = 1

	input_data[3][0] = 1
	input_data[3][1] = 1

	var label_data [trainingsize][output_dim]float64
	label_data[0][0] = 1
	label_data[0][1] = 0

	label_data[1][0] = 0
	label_data[1][1] = 1

	label_data[2][0] = 0
	label_data[2][1] = 1

	label_data[3][0] = 1
	label_data[3][1] = 0

	m = train(m, input_data, label_data, 0.01, 20000)

	var query = input_data[0]
	var answer outputvec
	answer = predict(m, query)

	print ("query : ", query[0], " ", query[1],"\n")
	print ("predicted: ", answer[0], " ",  answer[1], "\n")
	print ("actual: ", label_data[0][0], " ", label_data[0][1])
}