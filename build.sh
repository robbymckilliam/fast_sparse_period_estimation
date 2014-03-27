#!/bin/bash

#recurse() {
#    for D in `find -type d -not -name .`
#   do
#	pushd $D
#	recurse
#	popd
#    done
#    for f in *.mp
#    do
#	mpost -interaction=nonstopmode ${f}
#    done
#}

#compile all metapost files in this directory
#recurse .

pdflatex --shell-escape paper.tex
bibtex paper
pdflatex --shell-escape paper.tex
pdflatex --shell-escape paper.tex
