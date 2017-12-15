package org.ditw.graphProb.belUpdating

import org.ditw.graphProb.belUpdating.Potentials.Potential
import org.ditw.graphProb.belUpdating.ProbModels.ProbModel
import org.ditw.graphProb.belUpdating.TriangulatedGraphHelpers.VertexEdge
import org.jgrapht.graph.SimpleGraph

import scala.collection.mutable.ListBuffer
import scala.reflect.ClassTag

/**
  * Created by dev on 2017-12-07.
  */
object EnrichedGraphOps {

  private[graphProb] def add2CliqueSet(curr:ListBuffer[Set[String]], newCandidate:Set[String]):ListBuffer[Set[String]] = {
    var done = false
    var shouldAdd = true
    val it = curr.iterator
    var res = ListBuffer[Set[String]]()
    while (!done && it.hasNext) {
      val c = it.next()
      if (!c.subsetOf(newCandidate)) {
        if (newCandidate.subsetOf(c)) {
          shouldAdd = false
          done = true
        }
        res += c
      }

    }
    while (it.hasNext) {
      res += it.next()
    }
    if (shouldAdd) res += newCandidate
    res
  }

  private[graphProb] def removeSimplicialNodesFromClique[E <: VertexEdge : ClassTag](simplicialVtx:String, eg:EnrichedGraph[E])
    :(Set[String], Set[String], EnrichedGraph[E]) = {
    val cl = eg.family(simplicialVtx)
    val simpNodesInCl = cl.filter(eg.testSimplicial)
    val g1 = GraphHelpers.cloneSimpleGraph(eg._g)
    simpNodesInCl.foreach(g1.removeVertex)
    val newg = new EnrichedGraph[E](g1)
    (cl, simpNodesInCl, newg)
  }

  case class GrafData[T](id:String, data:T, desc:String)

  private[EnrichedGraphOps] def _toJoinTreeData(vertices:Set[String], step:Int, isSeparator:Boolean):GrafData[Set[String]] = {
    val d = vertices.toList.sorted
    val id = if (!isSeparator) s"V$step" else s"S$step"
    val desc = d.mkString("-")
    GrafData(id, vertices, desc)
  }

  type JoinTreeNodeData = GrafData[Set[String]]
  type JoinTreeEdgeData = GrafData[Set[String]]
  private[EnrichedGraphOps] def toJoinTreeNode(vertices:Set[String], step:Int):JoinTreeNodeData = {
    _toJoinTreeData(vertices, step, false)
  }
  private[EnrichedGraphOps] def toJoinTreeEdge(vertices:Set[String], step:Int):JoinTreeEdgeData = {
    _toJoinTreeData(vertices, step, true)
  }

  case class DecTree[NodeData, EdgeData](
    private[EnrichedGraphOps] val _nodes:Iterable[GrafData[NodeData]],
    private[EnrichedGraphOps] val _edges:Iterable[(String, String, GrafData[EdgeData])],
    private[EnrichedGraphOps] val _graph:SimpleGraph[String, VertexEdge]
  ) {
    private[EnrichedGraphOps] val nodeMap = _nodes.map(n => n.id -> n).toMap
    private[EnrichedGraphOps] val edgeMap = _edges.map(n => (n._1, n._2) -> n._3).toMap

    private[EnrichedGraphOps] val edges =
      _edges.map(p => if (p._1 > p._2) p._2 -> p._1 else p._1 -> p._2)

    def allEdges:Iterable[(String, String)] = edges

    private val nodeIds:Set[String] = _nodes.map(_.id).toSet
    def allNodes = nodeIds

    def edgeData(edge:(String,String)):GrafData[EdgeData] = {
      edgeMap(edge)
    }
    //private val edgeMap = _edges.map(n => n.id -> n).toMap

    import collection.mutable
    import collection.JavaConverters._
    def leafNodesFrom(tmpRoot:String):List[String] = {
      val visited = mutable.Set[String]()
      val toVisit = mutable.Set[String]()
      val leaves = ListBuffer[String]()
      toVisit.add(tmpRoot)

      while (toVisit.nonEmpty) {
        val n = toVisit.head
        toVisit.remove(n)

        val newVertice = _graph.edgesOf(n).asScala.map { edge =>
          (edge.vertices.toSet - n).head
        }.diff(visited)

        if (newVertice.isEmpty) leaves += n
        else toVisit ++= newVertice

        visited.add(n)
      }
      leaves.toList
    }
  }

  private def buildJoinTree[NodeData, EdgeData](
    d:(IndexedSeq[(GrafData[NodeData], GrafData[EdgeData])], List[(Int, Int)])
  ) :DecTree[NodeData, EdgeData] = {
    val nodes = d._1.map(_._1)
    //d._1.map(_._2)
    val g = GraphHelpers.createSimpleGraph[VertexEdge]
    nodes.foreach(n => g.addVertex(n.id))
    val edges = d._2.map { p =>
      val fromIdx = p._1
      val toIdx = p._2
      val fromNode = d._1(fromIdx)._1
      val toNode = d._1(toIdx)._1
      val edgeVtxs = List(fromNode.id, toNode.id).sorted
      g.addEdge(edgeVtxs(0), edgeVtxs(1))
      (edgeVtxs(0), edgeVtxs(1), d._1(fromIdx)._2)
    }
    DecTree(nodes, edges, g)
  }

  case class JunctionTreeNodeData(nodeIds:Set[String], potentials:Set[Potential])

  case class JunctionTreeEdgeData(edgeIds:Set[String], mailboxes:AnyRef)

  import collection.mutable
  def buildJunctionTree[NodeData, EdgeData](
    probModel:ProbModel
  ) :DecTree[JunctionTreeNodeData, JunctionTreeEdgeData] = {
    val g:SimpleGraph[String, VertexEdge] = GraphHelpers.graphFromModel(probModel)
    val eg = new EnrichedGraph(g)
    val jt:DecTree[Set[String], Set[String]] = eg.joinTree

    var nodeMap = mutable.Map[String, mutable.Set[Potential]]()
    probModel.potentials.foreach { pot =>
      val superSetNodes = jt._nodes.find(n => pot.allIds.subsetOf(n.data))
      if (superSetNodes.size != 1)
        throw new IllegalArgumentException("Expect 1 superset!!")
      val sn = superSetNodes.head
      if (!nodeMap.contains(sn.id)) nodeMap += sn.id -> mutable.Set(pot)
      else nodeMap(sn.id) += pot
    }

    val nodes:Iterable[GrafData[JunctionTreeNodeData]] = nodeMap.map(p =>
      GrafData(p._1, JunctionTreeNodeData(jt.nodeMap(p._1).data, p._2.toSet), jt.nodeMap(p._1).desc)
    )
    val edges:Iterable[(String, String, GrafData[JunctionTreeEdgeData])] = jt.edgeMap.map { p =>
      val eid = p._1
      val ed = jt.edgeData(eid)
      (eid._1, eid._2, GrafData(ed.id, JunctionTreeEdgeData(ed.data, null), ed.desc))
    }
    DecTree(nodes, edges, jt._graph)
  }

  class EnrichedGraphOps[E <: VertexEdge : ClassTag](private val _eg:EnrichedGraph[E]) {
    def findCliques:Set[Set[String]] = {
      var res = ListBuffer[Set[String]]()
      var next = _eg._eliminateOne
      var eg = _eg
      while (next.nonEmpty) {
        val (vtx, g1) = next.get
        res = add2CliqueSet(res, eg.family(vtx))
        eg = g1
        next = eg._eliminateOne
      }
      res.toSet
    }

    private[graphProb] def prepareJoinTree:IndexedSeq[(JoinTreeNodeData, JoinTreeEdgeData)] = {
      var res = ListBuffer[(JoinTreeNodeData, JoinTreeEdgeData)]()
      var next = _eg.firstSimplicialNode
      var eg = _eg
      var step = 1
      while (next.nonEmpty) {
        val vtx = next.get
        val (cl, removed, g1) = removeSimplicialNodesFromClique(vtx, eg)
        val nd = toJoinTreeNode(cl, step)
        val ed = toJoinTreeEdge(cl -- removed, step)
        res += nd -> ed
        eg = g1
        next = eg.firstSimplicialNode
        step = step + removed.size
      }
      res.toIndexedSeq
    }

    private[graphProb] def _genJoinTree:(IndexedSeq[(JoinTreeNodeData, JoinTreeEdgeData)], List[(Int, Int)]) = {
      val l = prepareJoinTree

      val links = ListBuffer[(Int, Int)]()
      l.indices.foreach { idx =>
        var start = idx+1
        var found = false
        val (cli, si) = l(idx)
        while (!found && start < l.size) {
          val (cl, s) = l(start)
          if (si.data.subsetOf(cl.data)) {
            val c = si.data -- s.data
            if (c.nonEmpty) {
              found = true
              links += idx -> start
            }
          }
          start = start + 1
        }
      }

      l -> links.toList
    }

    def joinTree:DecTree[Set[String], Set[String]] = buildJoinTree(_genJoinTree)
  }

  implicit def enrichedGraph2Ops[E <: VertexEdge : ClassTag](eg:EnrichedGraph[E]):EnrichedGraphOps[E] =
    new EnrichedGraphOps(eg)
}
