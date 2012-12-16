set terminal postscript enhanced eps font 30
set output 'eva-glickr-aggr.eps'
set size 1, 1
set xlabel 'Keywords ordered by multi-return AAT'
set xrange [0:101]
set ylabel 'AAT (ms)'
set yrange [500:10000]
set key bottom
set ytics ("500" 500, "" 600, "" 700, "" 800, "" 900, "1000" 1000, "2000" 2000, "" 3000, "4000" 4000, "" 5000, "" 6000, "" 7000, "8000" 8000, "" 9000, "10000" 10000)
set logscale y
plot "glickr-aggr.log" using 1:2 title 'Standard' with lines lw 3 lt 1, \
    "glickr-aggr.log" using 1:3 title 'Multi-return' with lines lw 3 lt 0

