set terminal postscript enhanced eps font 30
set size 1.2,1
set output "eva-glickr-indi.eps"
set xlabel "AAT (ms)"
set xrange [500:5000]
set logscale x
set ylabel "Cumulative %"
set yrange [0:100]
set key bottom
plot "glickr-indi.csv" using 1:(100/30.) s cumul title 'animals' with lines lw 3 lt 1, \
    "glickr-indi.csv" using 2:(100/30.) s cumul title 'architecture' with lines lw 3 lt 3, \
    "glickr-indi.csv" using 3:(100/30.) s cumul title 'art' with lines lw 3 lt 5, \
    "glickr-indi.csv" using 4:(100/30.) s cumul title 'autumn' with lines lw 3 lt 7

