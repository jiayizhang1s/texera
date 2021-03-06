package edu.uci.ics.amber.engine.architecture.breakpoint

import edu.uci.ics.amber.engine.common.tuple.ITuple

class FaultedTuple(val tuple: ITuple, val id: Long, val isInput: Boolean = false)
