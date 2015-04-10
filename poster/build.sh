#!/bin/bash
pushd ../code/data
mpost -interaction=nonstopmode posterplot.mp
popd
pdflatex -interaction=nonstopmode ITR_Poster.tex