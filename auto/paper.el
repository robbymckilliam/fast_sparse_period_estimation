(TeX-add-style-hook "paper"
 (lambda ()
    (LaTeX-add-bibliographies
     "bib")
    (LaTeX-add-labels
     "eq:periodicprocess"
     "eq:sigmodel"
     "fig_stat_model"
     "sec:peri-estim"
     "eq:gridwidthrecommended"
     "sec:quant-peri"
     "eq:boundaliasingP"
     "sec:comp-compl"
     "plot:benchmark"
     "plot:multipleN"
     "sec:simulations")
    (TeX-add-symbols
     '("pulsewithnode" 2)
     '("pulse" 1)
     '("rect" 1)
     "sgn"
     "sinc"
     "raxis"
     "vertgap"
     "ph"
     "T")
    (TeX-run-style-hooks
     "natbib"
     "sort&compress"
     "numbers"
     "comma"
     "square"
     "bm"
     "amsthm"
     "amssymb"
     "amsfonts"
     "amsmath"
     "pgfplots"
     "tikz"
     "mathbf-abbrevs"
     ""
     "latex2e"
     "IEEEtran10"
     "IEEEtran"
     "twoside"
     "twocolumn"
     "10pt"
     "defs")))

